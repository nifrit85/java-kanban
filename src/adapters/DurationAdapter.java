package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out.value(value.toSeconds());
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        return Duration.ofSeconds(Long.parseLong(in.nextString()));
    }
}
