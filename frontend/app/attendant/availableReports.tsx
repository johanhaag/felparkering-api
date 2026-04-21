import { View, Text } from "react-native";
import AttendantLargeReportWrapper from "../../components/AttendantLargeReportWrapper";
import { useCallback, useEffect, useState } from "react";
import { Report } from "../../types/report";
import { router, useFocusEffect } from "expo-router";
import { getApiMessage, useApi } from "../../services/api";
import axios from "axios";
import { useUser } from "../../context/UserContext";
import ReportTable from "../../components/ReportTable";
import Toast from "react-native-toast-message";
import { getAttendantActions } from "../../utils/attendantActions";

export default function AvailableReports() {
    const [activeReport, setActiveReport] = useState<Report | null>(null);
    const [newReports, setNewReports] = useState<Report[]>([]);
    const [currentPage, setCurrentPage] = useState(0); 
    const [numElements, setNumElements] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [sortBy, setSortBy] = useState("id");
    const [sortDir, setSortDir] = useState("asc"); 
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState("");

    const api = useApi();
    const {user} = useUser();

    useEffect(() => {
        if (!user) {
              router.replace("/");
        }
    }, [user]);

    const fetchReports = useCallback(async () => {
        try {
            const response = await api.getReports({
                page: currentPage,
                size: pageSize,
                search,
                sortBy,
                sortDir,
            });

            const reports: Report[] = response.data.content;

            setNewReports(reports);
            setCurrentPage(response.data.number);
            setNumElements(response.data.numberOfElements);
            setTotalElements(response.data.totalElements);
            setTotalPages(response.data.totalPages);
            setActiveReport(previous =>
                previous ? reports.find(report => report.id === previous.id) ?? null : previous
            );
        } catch (error: any) {
            if (axios.isAxiosError(error) && error.response) {
                console.log(error.response.data.error);
            }
        }
    }, [api, currentPage, pageSize, search, sortBy, sortDir]);

    useFocusEffect(
        useCallback(() => {
            fetchReports();
        }, [fetchReports])
    );

    const updateReportStatus = async (reportId: number, status: string) => {
        const update = await api.updateReport(reportId, { status });
        Toast.show({ type: "success", text1: getApiMessage(update) });
        await fetchReports();
    };

    const handleAssign = () => activeReport && updateReportStatus(activeReport.id, "ASSIGNED");
    const handleResolve = () => activeReport && updateReportStatus(activeReport.id, "RESOLVED");
    const handleUnassign = () => activeReport && updateReportStatus(activeReport.id, "NEW");

    const actions = activeReport
    ? getAttendantActions(activeReport, user?.id, {
          onAssign: handleAssign,
          onResolve: handleResolve,
          onUnassign: handleUnassign,
      })
    : [];

    return (
        <View className="flex-1 bg-park-background">
            <View className="flex-1 flex-row mt-4 px-4 gap-4">
                <View className="flex-[2]">
                    <ReportTable
                        currentUserId={user?.id} 
                        columns={["Id", "Address", "Violation", "Status", "Date"]}
                        data={newReports}
                        selected={activeReport}
                        onSelect={setActiveReport} 
                        currentPage={currentPage}
                        totalPages={totalPages} 
                        onPageChange={setCurrentPage}
                        numberOfElements={numElements} 
                        totalElements={totalElements}
                        sortBy={sortBy} 
                        sortDir={sortDir}   
                        onSortBy={setSortBy} 
                        onSortDir={setSortDir} 
                        pageSize={pageSize}
                        setPageSize={setPageSize}    
                        search={search}
                        setSearch={setSearch}       
                    />
                </View>
                {activeReport ? ( 
                    <><View className="flex-1">
                        <AttendantLargeReportWrapper
                            activeReport={activeReport}
                            actions={actions} />
                    </View></>
                ) : (
                    <></>
                )}
            </View>
        </View>
    )
}
