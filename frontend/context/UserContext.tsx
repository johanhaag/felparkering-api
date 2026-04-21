import { createContext, ReactNode, useContext, useEffect, useRef, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { decodeJwt, sanitizeToken } from "../utils/decodeJwt";
import { Text } from "react-native";
import { User } from "../types/User";

interface UserContextType {
    user: User | null;
    setUser: React.Dispatch<React.SetStateAction<User | null>>;
    logout: () => Promise<void>;
}

export const UserContext = createContext<UserContextType & { loading: boolean }>({
    user: null,
    setUser: () => {},
    logout: async () => {},
    loading: true,
});

export const UserProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

    const logout = async () => {
        try {
            await AsyncStorage.removeItem("token");
        } finally {
            setUser(null);
        } 
    };

    const loadUser = async () => {
        try {
            const stored = await AsyncStorage.getItem("token");
            const token = sanitizeToken(stored);
            if (!token) {
                setUser(null);
                return;
            }

            const payload = decodeJwt<any>(token);

            const now = Math.floor(Date.now() / 1000);
            if (typeof payload?.exp === "number" && payload.exp <= now) {
                await AsyncStorage.removeItem("token");
                setUser(null);
                return;
            }

            const userId = payload?.sub != null ? Number(payload.sub) : null;
            const userRole = payload?.role ?? null;

            if (userId == null || Number.isNaN(userId)) {
                await AsyncStorage.removeItem("token");
                return;
            }

            setUser({ id: userId, role: userRole });

        } catch (error) {
            console.warn("loadUser failed:", error);
            try { 
                await AsyncStorage.removeItem("token"); 
            } catch {}
            setUser(null);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadUser();

        intervalRef.current = setInterval(loadUser, 5 * 60 * 1000)
        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        }
    }, []);

    if (loading) {
        return <Text>Loading...</Text>;
    }

    return (
        <UserContext.Provider value={{ user, setUser, logout, loading }}>
            {children}
        </UserContext.Provider>
    );
}

export function useUser() {
    return useContext(UserContext);
}