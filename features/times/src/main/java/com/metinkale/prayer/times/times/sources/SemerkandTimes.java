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

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.metinkale.prayer.App;
import com.metinkale.prayer.times.times.Source;
import com.metinkale.prayer.times.times.Vakit;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SemerkandTimes extends WebTimes {
    
    @SuppressWarnings({"unused", "WeakerAccess"})
    public SemerkandTimes() {
        super();
    }
    
    @SuppressWarnings({"unused", "WeakerAccess"})
    public SemerkandTimes(long id) {
        super(id);
    }
    
    @NonNull
    @Override
    public Source getSource() {
        return Source.Semerkand;
    }
    
    protected boolean sync() throws ExecutionException, InterruptedException {
        LocalDate date = LocalDate.now();
        String _id = getId();
        
        final int year = LocalDate.now().getYear();
        
        char type = _id.charAt(0);
        String id = _id.substring(1);
        List<Day> result = Ion.with(App.get())
                .load("http://semerkandtakvimi.semerkandmobile.com/salaattimes?year=" + year + "&" + (type == 'c' ? "cityId=" : "districtId=") + id)
                .userAgent(App.getUserAgent()).as(new TypeToken<List<Day>>() {
                }).get();
        for (Day d : result) {
            date = date.withDayOfYear(d.DayOfYear);
            setTime(date, Vakit.FAJR, d.Fajr);
            setTime(date, Vakit.SUN, d.Tulu);
            setTime(date, Vakit.DHUHR, d.Zuhr);
            setTime(date, Vakit.ASR, d.Asr);
            setTime(date, Vakit.MAGHRIB, d.Maghrib);
            setTime(date, Vakit.ISHAA, d.Isha);
        }
        return result.size() > 25;
    }
    
    
    private static class Day {
        int DayOfYear;
        String Fajr;
        String Tulu;
        String Zuhr;
        String Asr;
        String Maghrib;
        String Isha;
    }
    
}
