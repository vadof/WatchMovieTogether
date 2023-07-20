import {Translation} from "./Translation";
import {Movie} from "./Movie";

export interface GroupSettings {
  selectedMovie: Movie
  movieProgress: string
  selectedTranslation: Translation
}
