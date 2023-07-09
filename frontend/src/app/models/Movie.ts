import {Resolution} from "./Resolution";
import {Translation} from "./Translation";

export interface Movie {
  link: string;
  name: string;
  translations: Translation[]
}
