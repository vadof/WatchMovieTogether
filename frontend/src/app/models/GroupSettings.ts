import {Translation} from "./Translation";
import {Movie} from "./Movie";
import {User} from "./User";

export interface GroupSettings {
  selectedMovie: Movie
  movieProgress: string
  selectedTranslation: Translation
  usersWithPrivileges: User[]
}
