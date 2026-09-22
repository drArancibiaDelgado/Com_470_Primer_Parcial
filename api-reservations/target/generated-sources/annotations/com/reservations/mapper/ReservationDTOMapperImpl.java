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
    date = "2026-09-22T15:14:22-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Microsoft)"
)
@Component
public class ReservationDTOMapperImpl implements ReservationDTOMapper {

    @Override
    public Reservation convert(ReservationDTO source) {
        if ( source == null ) {
            return null;
        }

        Reservation reservation = new Reservation();

        reservation.setPassengers( passengerDTOListToPassengerList( source.getPassengers() ) );
        reservation.setItinerary( itineraryDTOToItinerary( source.getItinerary() ) );
        reservation.setId( source.getId() );

        return reservation;
    }

    protected Passenger passengerDTOToPassenger(PassengerDTO passengerDTO) {
        if ( passengerDTO == null ) {
            return null;
        }

        Passenger passenger = new Passenger();

        passenger.setFirstName( passengerDTO.getFirstName() );
        passenger.setLastName( passengerDTO.getLastName() );
        passenger.setDocumentNumber( passengerDTO.getDocumentNumber() );
        passenger.setDocumentType( passengerDTO.getDocumentType() );
        passenger.setBirthday( passengerDTO.getBirthday() );

        return passenger;
    }

    protected List<Passenger> passengerDTOListToPassengerList(List<PassengerDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Passenger> list1 = new ArrayList<Passenger>( list.size() );
        for ( PassengerDTO passengerDTO : list ) {
            list1.add( passengerDTOToPassenger( passengerDTO ) );
        }

        return list1;
    }

    protected Segment segmentDTOToSegment(SegmentDTO segmentDTO) {
        if ( segmentDTO == null ) {
            return null;
        }

        Segment segment = new Segment();

        segment.setOrigin( segmentDTO.getOrigin() );
        segment.setDestination( segmentDTO.getDestination() );
        segment.setDeparture( segmentDTO.getDeparture() );
        segment.setArrival( segmentDTO.getArrival() );
        segment.setCarrier( segmentDTO.getCarrier() );

        return segment;
    }

    protected List<Segment> segmentDTOListToSegmentList(List<SegmentDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Segment> list1 = new ArrayList<Segment>( list.size() );
        for ( SegmentDTO segmentDTO : list ) {
            list1.add( segmentDTOToSegment( segmentDTO ) );
        }

        return list1;
    }

    protected Price priceDTOToPrice(PriceDTO priceDTO) {
        if ( priceDTO == null ) {
            return null;
        }

        Price price = new Price();

        price.setTotalPrice( priceDTO.getTotalPrice() );
        price.setTotalTax( priceDTO.getTotalTax() );
        price.setBasePrice( priceDTO.getBasePrice() );

        return price;
    }

    protected Itinerary itineraryDTOToItinerary(ItineraryDTO itineraryDTO) {
        if ( itineraryDTO == null ) {
            return null;
        }

        Itinerary itinerary = new Itinerary();

        itinerary.setSegment( segmentDTOListToSegmentList( itineraryDTO.getSegment() ) );
        itinerary.setPrice( priceDTOToPrice( itineraryDTO.getPrice() ) );

        return itinerary;
    }
}
