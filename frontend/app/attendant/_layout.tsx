import Icon from '@expo/vector-icons/Ionicons';
import { router, Tabs } from "expo-router";
import { Text, TouchableOpacity } from "react-native";
import { useUser } from "../../context/UserContext";
import Toast from 'react-native-toast-message';
import { SafeAreaProvider } from 'react-native-safe-area-context';

export default function AttendantLayout() {
    const { logout } = useUser();

    const handleLogout = async () => {
        await logout();
        router.replace("/");
    };

    return (
        <SafeAreaProvider>
            <Tabs
                screenOptions={{
                    headerStyle: {
                        backgroundColor: "#52796F",
                    },
                    headerTitleAlign: "center",
                    headerTintColor: "#fff",
                    headerLeft: () => (
                        <Text style={{ color: "#fff", marginLeft: 20, fontSize: 18 }}>
                            Felparkering API
                        </Text>
                    ),
                    headerRight: () => (
                        <TouchableOpacity onPress={handleLogout}>
                            <Text style={{ color: "#fff", marginRight: 20, fontWeight: 'bold', fontSize: 14 }}>
                                Log out
                            </Text>
                        </TouchableOpacity>
                    ),
                    tabBarActiveTintColor: "#52796F",
                }}
            >
                <Tabs.Screen
                        name="availableReports"
                        options={{
                            title: "Available Reports",
                            tabBarIcon: ({ color, size}) => (
                                <Icon name="document-text-outline" size={size} color={color}/>
                            )
                        }}
                    />
                <Tabs.Screen
                        name="myReports"
                        options={{
                            title: "My Reports",
                            tabBarIcon: ({ color, size}) => (
                                <Icon name="person-circle-outline" size={size} color={color}/>
                            )
                        }}
                    />
            </Tabs>
            <Toast />
        </SafeAreaProvider>
    );
}