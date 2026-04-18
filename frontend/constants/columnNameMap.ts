export const columnNameMap = [
    { index: 0, label: "Id", value: "id" },
    { index: 1, label: "Address", value: "address.street" },
    { index: 2, label: "Violation", value: "category" },
    { index: 3, label: "Status", value: "status" },
    { index: 4, label: "Date", value: "createdOn" },
]

export type ColumnNameMap = typeof columnNameMap[number]["value"];