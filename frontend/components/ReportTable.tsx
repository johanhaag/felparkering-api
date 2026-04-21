import { FlatList, Pressable, View, Text, TextInput } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import Icon from '@expo/vector-icons/Ionicons';
import { prettyAddress, prettyDate } from "../utils/prettyPrinter";
import { ReactElement, useState } from "react";
import { Report } from "../types/report";
import { parkingCategories } from "../constants/parkingCategories";
import { columnNameMap } from "../constants/columnNameMap";
import FixedDropdown from "./FixedDropdown";
import { pageSizes } from "../constants/pageSizes";

type TableProps = {
    currentUserId?: number;
    columns: string[];
    data: Report[];
    selected: Report | null;
    onSelect: (r: Report | null) => void;
    currentPage: number;
    totalPages: number;
    onPageChange: (page: number) => void;
    numberOfElements: number;
    totalElements: number;
    sortBy: string;
    sortDir: string;
    onSortBy: (column: string) => void;
    onSortDir: (direction: string) => void;
    pageSize: number;
    setPageSize: (size: number) => void;
    search: string;
    setSearch: (search: string) => void;
};

const getPagesToShow = (currentPage: number, totalPages: number) => {
    if (totalPages <= 3) {
        return [...Array(totalPages)].map((_, i) => i + 1);
    }

    if (currentPage <= 2) {
        return [1, 2, 3, "...", totalPages];
    }

    if (currentPage >= totalPages - 1) {
        return [1, "...", totalPages - 2, totalPages - 1, totalPages];
    }

    return [1, "...", currentPage, "...", totalPages];
};

type SortDir = "asc" | "desc";

export default function ReportTable({ 
    currentUserId,
    columns = [], 
    data = [], 
    selected, 
    onSelect,
    currentPage,
    totalPages, 
    onPageChange,
    numberOfElements, 
    totalElements,
    sortBy,
    sortDir,
    onSortBy,
    onSortDir,
    pageSize,
    setPageSize,
    search,
    setSearch
}: TableProps) {

    const COLUMN_FLEX = [1, 2, 2, 1, 1]; 
    const firstEntry = 1;
    const pages = getPagesToShow(currentPage, totalPages);
    const [searchFocused, setSearchFocused] = useState(false);

    const handleHeaderPress = (index: number) => {
        const key = columnNameMap.find(c => c.index === index)?.value ?? "Unknown column name";
        let nextDir: SortDir = "asc";

        if (sortBy === key && sortDir === "asc") {
            nextDir = "desc";
        }

        onSortBy(key);
        onSortDir(nextDir);
    };

    const TableHeader = (): ReactElement => (
        <View className="flex-row flex-1 min-w-full">
            {columns.map((c : string, index: number) => {
                const col = columnNameMap.find(c => c.index === index);
                const isActive = col?.value === sortBy;

                const iconName = !isActive
                    ? "chevron-expand-outline"
                    : sortDir === "asc"
                    ? "chevron-down-outline"
                    : "chevron-up-outline"

                return (
                    <Pressable
                        key={index}
                        style={{ flex: COLUMN_FLEX[index] }}
                        className={"p-4 border-b border-slate-200 bg-slate-50 flex-row items-center gap-1"}
                        onPress={() => handleHeaderPress(index)}
                    >
                        <Text 
                            className="text-sm uppercase font-semibold leading-none text-slate-500" 
                            numberOfLines={1} 
                            ellipsizeMode="tail"
                            selectable={false}
                        >
                            {c}
                        </Text>
                        <Icon name={iconName} size={14} color="#64748b"/>
                    </Pressable>
                );
            })}
        </View>
    );

    const TableRow = ({ item, isSelected }: { item: Report; isSelected: boolean; }) => {
        const values = [
            item.id,
            prettyAddress(item.address),
            parkingCategories.find(violation => violation.value === item.category)?.label ?? "Unknown violation",
            item.status,
            prettyDate(item.createdOn),
        ];

        const rowBg = isSelected ? "bg-gray-200" : "bg-white";
        console.log(currentUserId);
        const assignedToLabel = item.assignedToId === currentUserId ? item.assignedToId + " (You)" : item.assignedToId

        return (
            <Pressable 
                className={`${rowBg} flex-row hover:bg-slate-50 border-b border-slate-200`}
                onPress={() => onSelect(isSelected ? null : item)}
            >
                {values.map((value, index) => (
                    <View
                        key={index}
                        style={{ flex: COLUMN_FLEX[index] }}
                        className={"p-4 py-5"}
                    >
                        <Text 
                            className="block font-semibold text-sm text-slate-800" 
                            numberOfLines={1} 
                            ellipsizeMode="tail"
                        >
                            {value}
                        </Text>
                        {value === "ASSIGNED" ? (
                            <Text className="text-sm italic text-slate-700">
                                Attendant: {assignedToLabel}
                            </Text>
                        ) : (
                            <>
                            </>
                        )}
                    </View>
                ))}
            </Pressable>
        );
        
    };

    

    const TableFooter = () => {
        return (
            <View className="flex-row justify-between items-center px-4 py-3 bg-slate-150">
                <View className="flex-row items-center">
                    <Text className="text-slate-700">Page size:</Text>
                    <FixedDropdown items={pageSizes} input={pageSize} setInput={setPageSize}/>
                </View>
                
                <Text className="text-sm text-slate-700">
                    Viewing {firstEntry}-{numberOfElements} of {totalElements} reports
                </Text>
                <View className="flex-row space-x-1">
                    <Pressable 
                        className="px-3 py-1 min-w-9 min-h-9 text-sm font-normal text-slate-500 bg-white border border-slate-200 rounded hover:bg-slate-50 hover:border-slate-400 transition duration-200 ease"
                        disabled={currentPage===0}
                        onPress={() => onPageChange(currentPage - 1)}
                    >
                        <Text selectable={false}>Prev</Text>
                    </Pressable>
                    {pages.map((_, index) => (
                        <Pressable
                            key={index}
                            className="px-3 py-1 min-w-9 min-h-9 text-sm font-normal text-slate-500 bg-white border border-slate-200 rounded hover:bg-slate-50 hover:border-slate-400 transition duration-200 ease"
                            onPress={() => onPageChange(index)}
                        >
                            <Text>{index + 1}</Text>
                        </Pressable>
                    ))}
                    <Pressable 
                        className="px-3 py-1 min-w-9 min-h-9 text-sm font-normal text-slate-500 bg-white border border-slate-200 rounded hover:bg-slate-50 hover:border-slate-400 transition duration-200 ease"
                        disabled={currentPage===totalPages-1}
                        onPress={() => onPageChange(currentPage + 1)}
                    >
                        <Text selectable={false}>Next</Text>
                    </Pressable>
                </View>
            </View>
        );
    }

    return (
        <SafeAreaView style={{ flex: 1 }} className="w-full mb-3 mt-1 pl-3">
            <View className="flex-row items-center mb-2">
                <View className="flex-1 items-center">
                    <Text className="text-lg font-semibold text-slate-800">Active Reports</Text>
                </View>
                
                <View className="flex-[2] ml-3">
                    <View className="w-full bg-white">
                        <View className={`flex-row items-center rounded px-3 h-10 border ${searchFocused ? "border-slate-400 shadow-sm" : "border-slate-200"}`}>
                            <Icon name={"search-outline"} size={20} />
                            <TextInput 
                                className="flex-1 ml-2 text-sm text-slate-700 focus:outline-none"
                                placeholder="Search"
                                value={search}
                                onFocus={() => setSearchFocused(true)}
                                onBlur={() => setSearchFocused(false)}
                                onChangeText={value => setSearch(value)}
                            />
                            
                        </View>
                    </View>
                </View>
            </View>
                <View className="flex-1 w-full text-gray-700 bg-white rounded-lg shadow-md border shadow-gray-200 border-gray-200 bg-clip-border">
                    <FlatList 
                        showsVerticalScrollIndicator={false}
                        data={data} 
                        ListHeaderComponent={<TableHeader/>}
                        ListFooterComponent={<TableFooter/>}
                        ItemSeparatorComponent={() => <View className="h-[1px] bg-gray-200" />}
                        keyExtractor={({ id }) => String(id)}
                        renderItem={({ item }) => <TableRow item={item} isSelected={selected?.id === item.id}/>}
                        style={{flex: 1}}
                    />
                </View>
        </SafeAreaView>
    );
}