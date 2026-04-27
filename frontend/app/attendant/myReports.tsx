import { useCallback, useState } from "react";
import { useUser } from "../../context/UserContext";
import { getApiMessage, useApi } from "../../services/api";
import { useFocusEffect } from "expo-router";
import { Report } from "../../types/report";
import axios from "axios";
import AttendantReportWrapper from "../../components/AttendantReportWrapper";
import { prettyAddress } from "../../utils/prettyPrinter";
import { ScrollView, View, Text } from "react-native";
import AttendantLargeReportWrapper from "../../components/AttendantLargeReportWrapper";
import { parkingCategories } from "../../constants/parkingCategories";
import Toast from "react-native-toast-message";

export default function MyReports() {
    const [activeReport, setActiveReport] = useState<Report | null>(null);
    const [myReports, setMyReports] = useState([]);
    
    const api = useApi();
    const {user} = useUser();

    useFocusEffect(
            useCallback(() => {
            const fetchMyReports = async () => {
                try {
                    const response = await api.getReports({assignedToId: user?.id});
                    setMyReports(response.data);
                } catch (error: any) {
                    if (axios.isAxiosError(error) && error.response) {
                        console.log(error.response.data.error);
                    }
                }
            };
            fetchMyReports();
        }, [])
    );
    
    const handleResolve = async () => {
        if (!activeReport) return;

        try {
            const update = await api.updateReport(activeReport.id, { status: "RESOLVED" });
            Toast.show({ type: "success", text1: getApiMessage(update) });
            const res = await api.getReports({assignedTo: user?.email});
            setMyReports(res.data);
            setActiveReport(null);
        } catch (error) {
            Toast.show({ type: "error", text1: getApiMessage(error) });
        } 
    };

    const handleUnassign = async () => {
        if (!activeReport) return;

        try {
            const update = await api.updateReport(activeReport.id, { status: "NEW" });
            Toast.show({ type: "success", text1: getApiMessage(update) });
            const res = await api.getReports({assignedTo: user?.email});
            setMyReports(res.data);
            setActiveReport(null);  
        } catch (error) {
            Toast.show({ type: "error", text1: getApiMessage(error) });
        } 
    };

    return (
        <ScrollView className="m-4">
            <Text className="text-2xl">My assigned reports</Text>
            <ScrollView horizontal={true}>
                {myReports.map((report) => {
                    return (
                        <AttendantReportWrapper
                            key={report.id}
                            address={prettyAddress(report.address)} 
                            licensePlate={report.licensePlate} 
                            violation={parkingCategories.find(item => item.value === report.category)?.label ?? "Unknown violation"} 
                            timeStamp={report.createdOn}
                            onPress={() => setActiveReport(report)}
                            selected={activeReport?.address === report.address && activeReport?.licensePlate === report.licensePlate}
                        />
                    );
                })}
            </ScrollView>
            <View>
                {activeReport ? ( 
                    <>
                        <View className="border my-4 border-slate-200"></View><View className="w-2/6">
                            <AttendantLargeReportWrapper
                                primaryLabel="Resolve"
                                primaryAction={handleResolve}
                                secondaryLabel="Unassign"
                                secondaryAction={handleUnassign}
                                address={prettyAddress(activeReport.address)}
                                hq={activeReport.attendantGroup.name}
                                licensePlate={activeReport.licensePlate}
                                violation={parkingCategories.find(item => item.value === activeReport.category)?.label ?? "Unknown violation"}
                                timeStamp={activeReport.createdOn}
                                coords={[activeReport.address.latitude, activeReport.address.longitude]} />
                        </View>
                    </>
                ) : (
                    <></>
                )}
            </View>
        </ScrollView>
    )
}