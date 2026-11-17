import axios from "axios";
const api = axios.create({baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:1610/api",headers:{"Content-Type":"application/json"}});
api.interceptors.request.use(config=>{const token=localStorage.getItem("token");if(token)config.headers.Authorization=`Bearer ${token}`;return config;});
api.interceptors.response.use(r=>r,e=>{if(e.response?.status===401)console.warn("Authentication required/session expired");if(e.response?.status===403)console.warn("Permission denied");return Promise.reject(e);});
export default api;