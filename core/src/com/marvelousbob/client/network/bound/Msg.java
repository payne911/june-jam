package com.marvelousbob.client.network.bound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Msg {

    private String msg;
    private long timestamp;

    public Msg(String msg) {
        this.msg = msg;
        this.timestamp = -1;
    }
}
