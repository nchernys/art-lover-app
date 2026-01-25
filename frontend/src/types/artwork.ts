export interface ArtworkInterface {
  id: string;
  title: string;
  artistId: string;
  artist: string;
  year: string;
  movement: string;
  continent: string;
  country: string;
  description: string;
  imageKey: string;
  imageUrl: string;
  bookmark: boolean;
  image: File | null;
  userId: string;
}

export const ArtworkInitialState = {
  id: "",
  title: "",
  artistId: "",
  artist: "",
  year: "",
  continent: "",
  country: "",
  movement: "",
  description: "",
  bookmark: false,
  image: null,
  imageUrl: "",
  imageKey: "",
  userId: "",
};
