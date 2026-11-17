import api from "./axiosInstance";
export const loginUser=(credentials)=>api.post("/auth/login",credentials);
export const registerUser=(data)=>api.post("/users",data);