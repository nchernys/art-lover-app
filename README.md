# ART LOVER APP (in progress)

As an art enthusiast, I enjoy learning about artworks beyond simply viewing an image, including their historical context, artistic movements, and the stories behind them. However, museum descriptions are often limited to brief blurbs or basic facts such as the title, artist, country, and year of creation, which can feel disappointing and, at times, elitist—serving primarily those who already know a great deal about art.

To address this gap, I created **Art Lover**, an app designed to help art enthusiasts enjoy and understand art on the spot, without the need to search the web or jump between multiple pages while standing in a museum.

The Art Lover app is currently **web-based**, with **native mobile versions for iOS and Android planned**. It allows users to explore artworks through image uploads and keyword searches. By uploading an image of a painting, users receive the artist’s name, artwork title, artistic movement, historical period, and an AI-generated description that highlights the work’s stylistic and semantic features, as well as its contribution to the art world.

Users can explore further by clicking **Learn More** buttons to access additional information about the artist, movement, period, or artwork itself. For a more immersive experience, users can press **Play** to listen to the generated descriptions while viewing the artwork. Identified artworks and their generated descriptions can be saved to a personal gallery for later enjoyment.

---

## Tech Stack

- **Frontend:** React.js (Web)
- **Backend:** Java, Spring Boot
- **AI Services:** Gemini (AI description and image recognition)
- **Database:** MongoDB Atlas

---

## API Endpoints

### Authentication

| Method | Endpoint           | Description                       |
| ------ | ------------------ | --------------------------------- |
| POST   | `/api/auth/signup` | Create a new user account         |
| POST   | `/api/auth/login`  | Login and receive JWT cookie      |
| POST   | `/api/auth/logout` | Logout and clear JWT cookie       |
| GET    | `/api/auth/me`     | Get current authenticated user ID |

---

### Artworks

| Method | Endpoint                    | Description                                        |
| ------ | --------------------------- | -------------------------------------------------- |
| POST   | `/api/add`                  | Add a new artwork (multipart form, optional image) |
| GET    | `/api/show`                 | Get all artworks for authenticated user            |
| GET    | `/api/show/{id}`            | Get a single artwork by ID                         |
| PATCH  | `/api/update/{id}`          | Update artwork details and image                   |
| DELETE | `/api/delete/{id}`          | Delete an artwork                                  |
| PATCH  | `/api/update/bookmark/{id}` | Bookmark or unbookmark an artwork                  |

---

### Artists

| Method | Endpoint       | Description     |
| ------ | -------------- | --------------- |
| GET    | `/api/artists` | Get all artists |

---

### Search and AI

| Method | Endpoint                                | Description                           |
| ------ | --------------------------------------- | ------------------------------------- |
| GET    | `/api/search?keyword=`                  | Search artworks by keyword            |
| GET    | `/api/generate-description?userPrompt=` | Generate AI artwork description       |
| POST   | `/api/recognize`                        | Recognize artwork from uploaded image |

---

## Future Improvements

- **Native Mobile Apps**: iOS and Android applications to enable on-the-spot artwork exploration in museums.
- **Camera Snapshot Upload**: Allow users to capture and upload artwork images directly from the device camera.
- **Learn More Enhancements**: Expand and enrich detailed content for artworks, artists, movements, and periods.
- **Fun Facts Feature**: Provide random, engaging facts related to an artwork.
- **Face-Focused Image Preview**: Automatically center detected faces in gallery preview images to improve visual framing.

## Screenshots

### Artwork Recognition

![Home Page](artlover1.jpg)

### Gallery View

![Artwork Recognition](artlover2.jpg)

### Details View

![Gallery View](artlover3.jpg)
