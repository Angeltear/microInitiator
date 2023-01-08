package com.angeltear.microinitiator.Serializer;

import com.angeltear.microinitiator.Model.PaymentRequest;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;

public class PaymentRequestSerializer{
    private Kryo kryo = new Kryo();


    public byte[] encode(PaymentRequest codable) {
        kryo.register(PaymentRequest.class);
        Output output = new Output(4096, Integer.MAX_VALUE - 8);
        kryo.writeObject(output, codable);
        output.close();
        return output.toBytes();
    }

    public PaymentRequest decode(byte[] codedValue) {
        kryo.register(PaymentRequest.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(codedValue);
        Input input = new Input(bais);
        PaymentRequest readObject = kryo.readObject(input, PaymentRequest.class);
        return readObject;
    }


}
