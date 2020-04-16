/*
 * Copyright (c) 2013-2019 Metin Kale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metinkale.prayer.times.times.sources;

import androidx.annotation.NonNull;

import com.koushikdutta.ion.Ion;
import com.metinkale.prayer.App;
import com.metinkale.prayer.times.times.Source;
import com.metinkale.prayer.times.times.Vakit;

import org.joda.time.LocalDate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NVCTimes extends WebTimes {


    @SuppressWarnings({"unused", "WeakerAccess"})
    public NVCTimes() {
        super();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public NVCTimes(long id) {
        super(id);
    }

    public static String getName(String id) {
        try {
            URL url = new URL("http://namazvakti.com/XML.php?cityID=" + id);
            URLConnection ucon = url.openConnection();
            ucon.setConnectTimeout(3000);
            ucon.setReadTimeout(3000);

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);


            BufferedReader reader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));

            String line;


            while ((line = reader.readLine()) != null) {
                if (line.contains("cityNameTR")) {
                    line = line.substring(line.indexOf("cityNameTR"));
                    line = line.substring(line.indexOf("\"") + 1);
                    line = line.substring(0, line.indexOf("\""));
                    return line;

                }
            }
            reader.close();
        } catch (Exception ignore) {
        }
        return null;
    }

    @NonNull
    @Override
    public Source getSource() {
        return Source.NVC;
    }


    protected boolean sync() throws ExecutionException, InterruptedException {
        String result = Ion.with(App.get())
                .load("http://namazvakti.com/XML.php?cityID=" + getId())
                .userAgent(App.getUserAgent())
                .setTimeout(3000)
                .asString().get();
        int i = 0;
        String[] lines = result.split("\n");

        int y = LocalDate.now().getYear();
        for (String line : lines) {
            if (line.contains("<prayertimes")) {
                String day = line.substring(line.indexOf("day=") + 5);
                String month = line.substring(line.indexOf("month=") + 7);
                day = day.substring(0, day.indexOf("\""));
                month = month.substring(0, month.indexOf("\""));
                if (day.length() == 1) {
                    day = "0" + day;
                }
                if (month.length() == 1) {
                    month = "0" + month;
                }
                StringBuilder data = new StringBuilder(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                data = new StringBuilder(data.toString().replace("*", "").replace("\t", " "));
                List<String> d = new ArrayList<>(Arrays.asList(data.toString().split(" ")));
                String sabah = d.get(1);
                String asrSani = d.get(7);
                d.remove(15);
                d.remove(14);
                d.remove(13);
                d.remove(12);
                d.remove(10);
                d.remove(8);
                d.remove(7);
                d.remove(4);
                d.remove(3);
                d.remove(1);
                d.add(sabah);
                d.add(asrSani);
                data = new StringBuilder();
                for (String s : d) {
                    if (s.length() == 4) {
                        data.append(" 0").append(s);
                    } else {
                        data.append(" ").append(s);
                    }
                }

                String[] array = data.substring(1).split(" ");
                LocalDate localDate = new LocalDate(y, Integer.parseInt(month), Integer.parseInt(day));
                setTime(localDate, Vakit.FAJR, array[0]);
                setTime(localDate, Vakit.SUN, array[1]);
                setTime(localDate, Vakit.DHUHR, array[2]);
                setTime(localDate, Vakit.ASR, array[3]);
                setTime(localDate, Vakit.MAGHRIB, array[4]);
                setTime(localDate, Vakit.ISHAA, array[5]);
                setSabah(localDate, sabah);
                setAsrThani(localDate, asrSani);
                i++;
            }

        }
        return i > 25;
    }


}
