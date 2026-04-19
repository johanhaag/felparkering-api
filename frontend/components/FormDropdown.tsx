import { useEffect, useRef, useState } from "react";
import { FlatList, Text, TouchableOpacity, View } from "react-native";
import FormTextField from "./FormTextField";

interface DropdownItem {
    index: string;
    label: string;
    value?: string;
}

interface FormDropdownProps {
    items: DropdownItem[];
    placeholder?: string;
    value?: string;
    onChange?: (value: string) => void;
    iconName?: any;
    error?: string;
    allowCustomInput?: boolean;
}

export default function FormDropdown({ items, placeholder, value, onChange, iconName, error, allowCustomInput }: FormDropdownProps) {
    const [dropdown, toggleDropdown] = useState(false);
    const [inputText, setInputText] = useState("");
    const [focused, setFocused] = useState(false);
    const [filteredItems, setFilteredItems] = useState<DropdownItem[]>([]);

    const wasSelected = useRef(false);
    const didMount = useRef(false);

    useEffect(() => {
        if (!didMount.current) {
            didMount.current = true;
            return;
        }
        
        if (!allowCustomInput) {
            const selected = items.find((item) => item.value === value);
            setInputText(selected?.label || "");
        } else if (value !== inputText) {
            setInputText(value || "");
        }
    }, [value, items])

    useEffect(() => {
        if (wasSelected.current) {
            wasSelected.current = false;
            return;
        }

        const anyMatch = items.filter((item) =>
            item.label.toLowerCase().includes(inputText.toLowerCase())
        );

        setFilteredItems(anyMatch);
        toggleDropdown(anyMatch.length > 0);

    }, [inputText]);

    const handleSelect = (item: DropdownItem) => {
        wasSelected.current = true;
        setInputText(item.label);
        toggleDropdown(false);
        onChange?.(item.value);
    };

    return (
        <View>
            <FormTextField
                placeholder={placeholder}
                value={inputText}
                onChangeText={(text) => {
                    setInputText(text);
                    if (allowCustomInput) {
                        onChange?.(text);
                    }
                }}
                onFocus={() => setFocused(true)}
                onBlur={() => {
                    setTimeout(() => setFocused(false), 150);

                    const matched = items.find((item) => item.label.toLowerCase() === inputText.trim().toLowerCase());

                    if (matched) {
                        onChange?.(matched.value);
                    } else {
                        onChange?.(null);
                    }
                    
                }}
                iconName={iconName}
                error={error}
            />

            {focused && dropdown && (
                <View className="mx-2 border border-gray-300 rounded-b bg-white shadow-md max-h-60">
                    <FlatList 
                        data={filteredItems} 
                        keyExtractor={(item) => item.index}
                        keyboardShouldPersistTaps="handled"
                        renderItem={({ item }) => 
                            <TouchableOpacity
                                className="px-4 py-2 border-t border-gray-100 hover:bg-gray-100"
                                onPress={() => handleSelect(item)}
                            >
                                <Text className="text-gray-800">{item.label}</Text>
                            </TouchableOpacity> 
                        }
                    />
                </View>
                )}
        </View>
    );
}