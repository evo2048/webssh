import { Server } from "../server-model/server";

export class userDetails {
    userId: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    servers: Server[];
}