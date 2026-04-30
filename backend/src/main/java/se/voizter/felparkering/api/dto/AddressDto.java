package se.voizter.felparkering.api.dto;

import java.util.List;

import se.voizter.felparkering.api.model.Address;

public record AddressDto(
    Long id,
    double longitude,
    double latitude,
    String street,
    List<String> houseNumbers,
    String city,
    double distanceFromCity
) {
    public static AddressDto fromEntity(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressDto(
            address.getId(),
            address.getLongitude(),
            address.getLatitude(),
            address.getStreet(), 
            address.getHouseNumbers(),
            address.getCity(),
            address.getDistanceFromCity()
        );
    }
}
