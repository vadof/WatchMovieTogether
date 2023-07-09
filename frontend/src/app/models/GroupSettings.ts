import {Translation} from "./Translation";
import {Resolution} from "./Resolution";
import {Movie} from "./Movie";

export interface GroupSettings {
  selectedMovie: Movie
  movieProgress: string
  selectedTranslation: Translation
}
