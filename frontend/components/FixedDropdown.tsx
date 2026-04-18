import { useState } from "react";
import { View } from "react-native";
import { Dropdown } from 'react-native-element-dropdown';
import { PageSizes } from "../constants/pageSizes";

interface FixedDropdownProps {
    items: PageSizes;
    input: number;
    setInput: (n: number) => void;
}

export default function FixedDropdown({ items, input, setInput } : FixedDropdownProps) {

    return (
        <View className="p-2 border-b border-slate-300">
            <Dropdown
                data={items}
                labelField={"label"}
                valueField={"value"}
                value={input}
                dropdownPosition={'top'}
                placeholder={input.toString()}
                onChange={(item) => {
                    setInput(item.value);
                }}
            />
        </View>
    )
}