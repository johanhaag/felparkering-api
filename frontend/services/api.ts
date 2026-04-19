import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { useUser } from "../context/UserContext";

export function getApiMessage(input: any, fallback = "Something went wrong") {
    if (!input) return fallback;
    
    const axiosMsg =
        input?.response?.data?.error ??
        input?.response?.data?.message ??
        input?.response?.data ??
        input?.response ??
        undefined;
    
    if (typeof axiosMsg === "string") return axiosMsg;

    const directMsg = 
        input?.data?.error ??
        input?.data?.message ??
        undefined;
    
    if (typeof directMsg === "string") return directMsg;

    if (typeof input === "string") return input;

    if (input instanceof Error) return input.message;

    return fallback;
}

export function useApi() {
    const { setUser } = useUser();

    const API_URL = process.env.EXPO_PUBLIC_API_BASE ?? "http://localhost:8080";
    const api = axios.create({
        baseURL: API_URL,
        headers: {
            'Content-Type': 'application/json'
        },
    });

    api.interceptors.request.use(
        async config => {
            if (config.url?.includes("/login")) return config;
            const t = await AsyncStorage.getItem("token");
            if (t) config.headers.Authorization = `Bearer ${t}`;
            return config;
        }, error => Promise.reject(error)
    );


    api.interceptors.response.use(
        response => response,
        async error => {
            if (error.response?.status === 401) {
                await AsyncStorage.removeItem("token");
                setUser(null);
            }
            return Promise.reject(error);
        }
    );

    return {
        login: (data: any) => api.post('/login', data),
        register: (data: any) => api.post('/register', data),
        createReport: (data: any) => api.post('/reports', data),
        getReports: (params?: {page?: number, size?: number, search?: string, sortBy?: string, sortDir?: string, status?: string, assignedTo?: string}) => api.get('/reports', {
            params: {
                page: params?.page,
                size: params?.size,
                search: params?.search,
                sortBy: params?.sortBy,
                sortDir: params?.sortDir,
                status: params?.status,
                assignedTo: params?.assignedTo,
            },
        }),
        getReport: (id: number) => api.get(`/reports/${id}`),
        updateReport: (id: number, data: { status: string }) => api.put(`/reports/${id}`, data),
        createAttendant: (data: any) => api.post('admin/attendant', data),
        getAllUsers: () => api.get('/admin/users'),
        deleteUser: (id: number) => api.delete(`/admin/users/${id}`),
        createAttendantGroup: (data: any) => api.post('/attendants', data),
        getAllAttendantGroups: () => api.get('/attendants'),
        deleteAttendantGroup: (id: number) => api.delete(`/attendants/${id}`),
        searchAddress: (query: string) => api.get(`/addresses/search`, { params: { query } }),
        getRoute: (payload: { start: [number, number]; end: [number, number] }, cfg?: any) => api.post('/addresses/route', payload, cfg),
    };
}