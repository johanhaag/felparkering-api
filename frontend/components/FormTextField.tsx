import Icon from '@expo/vector-icons/Ionicons';
import { useState } from 'react';
import { TextInput, TextInputProps, View, Text } from "react-native";

interface FormTextFieldProps extends TextInputProps {
  iconName?: any;
  error?: string;
}

export default function FormTextField({ iconName, error, ...props }: FormTextFieldProps) {
    const [focused, setFocused] = useState(false);
    return (
        <View className="m-2">
            <View 
                className={`w-full flex-row items-center border rounded px-4 py-2 flex-1 mb-1 ${
                    focused ? "border-blue-500" : error ? "border-red-500" : "border-gray-300"
                }`}
            >
                {iconName && <Icon name={iconName} size={20} className="mr-1 text-park-icon"/>}
                <TextInput
                    placeholderTextColor={"#bdbdbd"}
                    className="flex-1 text-gray-800 bg-transparent focus:outline-none"
                    onFocus={() => setFocused(true)}
                    onBlur={() => setFocused(false)}
                    {...props}
                />
            </View>
            {error && (
                <Text className="text-red-500 text-xs">{error}</Text>
            )}
        </View>
    );
}
