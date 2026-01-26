export interface ArtworkSearchResultInterface {
  title: string;
  artist: string;
  year: string;
  movement: string;
  continent: string;
  country: string;
  description: string;
  imageUrls: string[];
  box: ArtworkBoxBounds;
  bookmark: boolean;
  userId: string;
}

type ArtworkBoxBounds = {
  x: number;
  y: number;
  width: number;
  height: number;
};
