package com.example.patrycja.filmbase.DTO.maps;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.model.Actor;

public class MapActorItemDTO {

    private ActorDTO actor;
    private double rate;

    public MapActorItemDTO() {}

    public MapActorItemDTO(Actor actor, double rate) {
        this.actor = new ActorDTO(actor);
        this.rate = rate;
    }

    public ActorDTO getActor() {
        return actor;
    }

    public void setActor(ActorDTO actor) {
        this.actor = actor;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
