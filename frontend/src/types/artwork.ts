export interface ArtworkInterface {
  id: string;
  title: string;
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
}

export const ArtworkInitialState = {
  id: "",
  title: "",
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
};