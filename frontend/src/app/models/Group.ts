import {User} from "./User";

export interface Group {
  id: number
  name: string
  admin: string
  currentMovie: string,
  movieProgress: number,
  users: User[]
}
