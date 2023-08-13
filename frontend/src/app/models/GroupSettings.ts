import {Movie} from "./Movie";
import {User} from "./User";
import {MovieSettings} from "./MovieSettings";
import {SeriesSettings} from "./SeriesSettings";

export interface GroupSettings {
  movieSettings: MovieSettings;
  seriesSettings: SeriesSettings;
  usersWithPrivileges: User[];
}
