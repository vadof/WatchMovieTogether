import {Movie} from "../models/Movie";
import {Translation} from "../models/Translation";

export interface MovieSelectionObject {
  groupId: number
  movie: Movie
  selectedTranslation: Translation
}
