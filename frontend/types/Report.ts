import type { ParkingViolationValue } from "../constants/parkingCategories";

export interface Report {
    id: number,
    address: Address;
    licensePlate: string;
    category: ParkingViolationValue;
    attendantGroup: any;
    assignedToId: number;
    createdOn: string;
    updatedOn: string;
    status: string;
}

export interface Address {
    id: number,
    distanceFromCity: number,
    latitude: number,
    longitude: number,
    street: string;
    houseNumbers: string[];
    city: string;
}