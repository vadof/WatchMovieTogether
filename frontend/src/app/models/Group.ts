import {User} from "./User";

export interface Group {
  id: number
  name: string
  admin: string
  selectedMovieSettings: string,
  users: User[]
}
