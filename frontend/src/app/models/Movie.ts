import {Resolution} from "./Resolution";
import {Translation} from "./Translation";

export interface Movie {
  type: 'movie'
  link: string;
  name: string;
  translations: Translation[]
}
