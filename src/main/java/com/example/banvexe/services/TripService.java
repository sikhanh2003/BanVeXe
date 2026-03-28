package com.example.banvexe.services;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    // Lấy tất cả chuyến xe (không phân trang - nếu cần dùng cho các logic khác)
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    // MỚI: Lấy danh sách chuyến xe có phân trang (10 dòng/trang)
    public Page<Trip> getAllTripsPaginated(int page, int size) {
        // Sort.by("departureTime").descending() giúp hiện chuyến mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").descending());
        return tripRepository.findAll(pageable);
    }

    public Trip createTrip(Trip trip) {
        // Logic: Nếu số ghế trống gửi lên bị trống, lấy từ sức chứa của xe khách
        if (trip.getAvailableSeats() == null && trip.getBus() != null) {
            trip.setAvailableSeats(trip.getBus().getCapacity());
        }
        return tripRepository.save(trip);
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }
}