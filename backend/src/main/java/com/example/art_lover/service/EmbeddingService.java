package com.example.art_lover.service;

import com.google.cloud.vertexai.api.EndpointName;
import com.google.cloud.vertexai.api.PredictRequest;
import com.google.cloud.vertexai.api.PredictResponse;
import com.google.cloud.vertexai.api.PredictionServiceClient;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

        private static final String PROJECT_ID = "gen-lang-client-0213855665";
        private static final String LOCATION = "us-central1";
        private static final String MODEL_ID = "text-embedding-004";

        private final PredictionServiceClient client;
        private final EndpointName endpointName;

        public EmbeddingService() throws IOException {
                this.client = PredictionServiceClient.create();
                this.endpointName = EndpointName.ofProjectLocationEndpointName(
                                PROJECT_ID,
                                LOCATION,
                                MODEL_ID);
        }

        public List<Float> embed(String text) {
                // Build instance payload
                Struct instance = Struct.newBuilder()
                                .putFields(
                                                "content",
                                                Value.newBuilder().setStringValue(text).build())
                                .build();

                PredictRequest request = PredictRequest.newBuilder()
                                .setEndpoint(endpointName.toString())
                                .addInstances(Value.newBuilder().setStructValue(instance).build())
                                .build();

                PredictResponse response = client.predict(request);

                // Parse embeddings
                List<Float> vector = new ArrayList<>(768);

                Struct embeddingStruct = response.getPredictions(0).getStructValue();

                ListValue values = embeddingStruct
                                .getFieldsOrThrow("embeddings")
                                .getStructValue()
                                .getFieldsOrThrow("values")
                                .getListValue();

                for (Value v : values.getValuesList()) {
                        vector.add((float) v.getNumberValue());
                }

                return vector;
        }
}
