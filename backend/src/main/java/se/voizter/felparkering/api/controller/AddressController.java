package se.voizter.felparkering.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import se.voizter.felparkering.api.dto.AddressSuggestionDto;
import se.voizter.felparkering.api.dto.ApiResponse;
import se.voizter.felparkering.api.dto.RouteRequest;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.service.AddressService;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AddressSuggestionDto>>> search(@RequestParam String query) {
        List<AddressSuggestionDto> addresses = addressService.getAddresses(query);

        return ResponseEntity.ok(
            new ApiResponse<>(
                addresses,
                Message.ADDRESSES_FETCHED.toString()
            )
        );
    }

    @PostMapping("/route")
    public ResponseEntity<ApiResponse<Map<?, ?>>> getRoute(@Valid @RequestBody RouteRequest request) {
        Map<?, ?> route = addressService.getRoute(request.start(), request.end());

        return ResponseEntity.ok(
            new ApiResponse<>(
                route,
                Message.ROUTE_FETCHED.toString()
            )
        );
    }
}
