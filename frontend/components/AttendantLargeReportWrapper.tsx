import { View, Text, Pressable } from "react-native";
import WebMap from "./WebMap";
import { cities } from "../constants/cities";
import { useCallback, useState } from "react";
import Chip from "./Chip";
import { prettyAddress, prettyDistance, prettyDuration } from "../utils/prettyPrinter";
import { Report } from "../types/report";
import { parkingCategories } from "../constants/parkingCategories";
import { ReportAction } from "../utils/attendantActions";

interface AttendantLargeReportWrapperProps {
    activeReport: Report;
    actions: ReportAction[];
}

export default function AttendantLargeReportWrapper({ 
    activeReport,
    actions
}: AttendantLargeReportWrapperProps) {

    const [routeSummary, setRouteSummary] = useState<{distance: number; duration: number} | null>(null);

    const handleRouteReady = useCallback((summary?: {distance: number; duration: number}) => {
        if (!summary) return;
        setRouteSummary({
            distance: summary.distance ?? 0,
            duration: summary.duration ?? 0,
        });
    }, []);

    const coords: [number, number] = [activeReport.address.latitude, activeReport.address.longitude];
    const violation = parkingCategories.find(item => item.value === activeReport.category)?.label ?? "Unknown violation";

    function prettyDate(ts?: string) {
        if (!ts) return "";
        const d = new Date(ts);
        return `${d.getFullYear()}-${(d.getMonth() + 1)
            .toString()
            .padStart(2, "0")}-${d.getDate().toString().padStart(2, "0")}, ${d
            .getHours()
            .toString()
            .padStart(2, "0")}:${d.getMinutes().toString().padStart(2, "0")}`;
    }

    function getCityCoordinates(cityName: string) {
        const match = cities.find(c => c.city.toLowerCase() === cityName.toLowerCase());
        return match?.position || null;
    }
    
    const hqPosition = getCityCoordinates(activeReport.attendantGroup.name);

    return (
        <View className="w-full">
                <View className="relative rounded-lg p-4 shadow-md border shadow-gray-200 border-gray-200 bg-white overflow-hidden divide-y divide-slate-100">
                    <View className="px-5 py-4">
                        <Text className="text-2xl font-semibold text-slate-900">{prettyAddress(activeReport.address)}</Text>
                    </View>  

                    <View className="border-t border-slate-100" />

                    <View className="h-80">
                        <WebMap adressPosition={coords} hqPosition={[hqPosition.latitude, hqPosition.longitude]} onRouteReady={handleRouteReady}/>
                    </View>

                    <View className="border-t border-slate-100" />

                    <View className="px-5 py-4 gap-3">
                        <View className="flex-row flex-wrap items-center gap-2">
                            {routeSummary && (
                                <>
                                    <Chip label={activeReport.licensePlate} iconName="car-outline"/>
                                    <Chip label={prettyDistance(routeSummary.distance)} iconName="speedometer-outline"/>
                                    <Chip label={prettyDuration(routeSummary.duration)} iconName="time-outline"/>
                                </>
                            )}
                        </View>
                        <Text className="text-base text-slate-900">{violation}</Text>
                        <Text className="text-sm text-slate-500">{prettyDate(activeReport.createdOn)}</Text>
                    </View>

                    <View className="border-t border-slate-100" />

                    <View className="px-5 py-4 flex-row items-center justify-between gap-3">
                        {actions.map(action => (
                            <Pressable 
                                key={action.key}
                                onPress={action.onPress} 
                                disabled={action.disabled}
                                className={
                                    action.variant === "primary"
                                        ? "rounded-xl px-5 py-3 bg-emerald-600"
                                        : action.variant === "danger"
                                        ? "rounded-xl px-5 py-3 bg-red-600"
                                        : "rounded-xl px-5 py-3 border border-slate-200"
                                }
                            >
                                <Text className={action.variant === "secondary" ? "text-slate-700" : "text-white"}>
                                    {action.label}
                                </Text>
                            </Pressable>
                        ))}
                    </View>
                </View>
        </View>
    );
}
