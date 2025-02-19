package com.stulikov.public_transport_stops_on_spring;

import com.stulikov.public_transport_stops_on_spring.cache.Cache;
import com.stulikov.public_transport_stops_on_spring.exceptions.ConnectException;
import com.stulikov.public_transport_stops_on_spring.forecast.Forecast;
import com.stulikov.public_transport_stops_on_spring.stop.Stop;
import com.stulikov.public_transport_stops_on_spring.stop.Stops;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;


@Controller
public class MainController {

    private List<Stop> matches;

    private Request request;

    @Autowired
    public MainController(Request request) {
        this.request = request;
    }

    @GetMapping("/inputStop")
    public String inputStopNameFromUser() {
        return "inputStopName";
    }

    @GetMapping("/matches")
    public String showMatches(@RequestParam(name = "userStop") String userStop, Model model)   {
        matches = Stops.getMatches(userStop);
        model.addAttribute("stopMatches", matches);

        return "showMatches";
    }

    @GetMapping("/arrivalTimeAndCurrentPosition")
    public String showArrivalTimeAndCurrentPosition(@RequestParam(name = "matchNumber") int matchNumber, Model model) throws IOException, ConnectException {
        int stopNumber = Stops.getStopID(matches.get(matchNumber - 1));
        List<Forecast> forecastsFromCache = Cache.searchAndGet(stopNumber);
        if (forecastsFromCache != null) {
            model.addAttribute("response", forecastsFromCache);
        } else {
            List<Forecast> forecastsFromServer = request.httpRequest(stopNumber);
            model.addAttribute("response", forecastsFromServer);
            Cache.add(stopNumber, forecastsFromServer);
        }
        return "showArrivalTimeAndCurrentPosition";
    }
}