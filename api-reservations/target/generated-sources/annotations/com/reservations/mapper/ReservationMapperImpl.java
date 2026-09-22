package com.reservations.mapper;

import com.reservations.dto.ItineraryDTO;
import com.reservations.dto.PassengerDTO;
import com.reservations.dto.PriceDTO;
import com.reservations.dto.ReservationDTO;
import com.reservations.dto.SegmentDTO;
import com.reservations.model.Itinerary;
import com.reservations.model.Passenger;
import com.reservations.model.Price;
import com.reservations.model.Reservation;
import com.reservations.model.Segment;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-22T15:14:23-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Microsoft)"
)
@Component
public class ReservationMapperImpl implements ReservationMapper {

    @Override
    public ReservationDTO convert(Reservation source) {
        if ( source == null ) {
            return null;
        }

        ReservationDTO reservationDTO = new ReservationDTO();

        reservationDTO.setPassengers( passengerListToPassengerDTOList( source.getPassengers() ) );
        reservationDTO.setItinerary( itineraryToItineraryDTO( source.getItinerary() ) );
        reservationDTO.setId( source.getId() );

        return reservationDTO;
    }

    protected PassengerDTO passengerToPassengerDTO(Passenger passenger) {
        if ( passenger == null ) {
            return null;
        }

        PassengerDTO passengerDTO = new PassengerDTO();

        passengerDTO.setFirstName( passenger.getFirstName() );
        passengerDTO.setLastName( passenger.getLastName() );
        passengerDTO.setDocumentNumber( passenger.getDocumentNumber() );
        passengerDTO.setDocumentType( passenger.getDocumentType() );
        passengerDTO.setBirthday( passenger.getBirthday() );

        return passengerDTO;
    }

    protected List<PassengerDTO> passengerListToPassengerDTOList(List<Passenger> list) {
        if ( list == null ) {
            return null;
        }

        List<PassengerDTO> list1 = new ArrayList<PassengerDTO>( list.size() );
        for ( Passenger passenger : list ) {
            list1.add( passengerToPassengerDTO( passenger ) );
        }

        return list1;
    }

    protected SegmentDTO segmentToSegmentDTO(Segment segment) {
        if ( segment == null ) {
            return null;
        }

        SegmentDTO segmentDTO = new SegmentDTO();

        segmentDTO.setOrigin( segment.getOrigin() );
        segmentDTO.setDestination( segment.getDestination() );
        segmentDTO.setDeparture( segment.getDeparture() );
        segmentDTO.setArrival( segment.getArrival() );
        segmentDTO.setCarrier( segment.getCarrier() );

        return segmentDTO;
    }

    protected List<SegmentDTO> segmentListToSegmentDTOList(List<Segment> list) {
        if ( list == null ) {
            return null;
        }

        List<SegmentDTO> list1 = new ArrayList<SegmentDTO>( list.size() );
        for ( Segment segment : list ) {
            list1.add( segmentToSegmentDTO( segment ) );
        }

        return list1;
    }

    protected PriceDTO priceToPriceDTO(Price price) {
        if ( price == null ) {
            return null;
        }

        PriceDTO priceDTO = new PriceDTO();

        priceDTO.setTotalPrice( price.getTotalPrice() );
        priceDTO.setTotalTax( price.getTotalTax() );
        priceDTO.setBasePrice( price.getBasePrice() );

        return priceDTO;
    }

    protected ItineraryDTO itineraryToItineraryDTO(Itinerary itinerary) {
        if ( itinerary == null ) {
            return null;
        }

        ItineraryDTO itineraryDTO = new ItineraryDTO();

        itineraryDTO.setSegment( segmentListToSegmentDTOList( itinerary.getSegment() ) );
        itineraryDTO.setPrice( priceToPriceDTO( itinerary.getPrice() ) );

        return itineraryDTO;
    }
}
