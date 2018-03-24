package com.example.patrycja.filmbase.DTO.maps;

import com.example.patrycja.filmbase.DTO.ActorDTO;
import com.example.patrycja.filmbase.model.Actor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MapActorItemDTO {

    private ActorDTO actor;
    private double rate;

    public MapActorItemDTO(Actor actor, double rate) {
        this.actor = new ActorDTO(actor);
        this.rate = rate;
    }
}
