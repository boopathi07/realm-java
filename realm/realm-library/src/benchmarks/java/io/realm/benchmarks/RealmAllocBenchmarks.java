/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.benchmarks;

import android.support.test.InstrumentationRegistry;

import org.junit.runner.RunWith;

import dk.ilios.spanner.AfterExperiment;
import dk.ilios.spanner.BeforeExperiment;
import dk.ilios.spanner.Benchmark;
import dk.ilios.spanner.BenchmarkConfiguration;
import dk.ilios.spanner.SpannerConfig;
import dk.ilios.spanner.junit.SpannerRunner;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.benchmarks.config.BenchmarkConfig;
import io.realm.entities.AllTypes;
import io.realm.entities.Dog;

@RunWith(SpannerRunner.class)
public class RealmAllocBenchmarks {
    @BenchmarkConfiguration
    public SpannerConfig configuration = BenchmarkConfig.getConfiguration(this.getClass().getCanonicalName());

    private Realm realm;

    @BeforeExperiment
    public void before() {
        RealmConfiguration config = new RealmConfiguration.Builder(InstrumentationRegistry.getTargetContext()).build();
        Realm.deleteRealm(config);
        realm = Realm.getInstance(config);
        realm.beginTransaction();
        realm.createObject(AllTypes.class).getColumnRealmList().add(realm.createObject(Dog.class));
        realm.commitTransaction();
    }

    @AfterExperiment
    public void after() {
        realm.close();
    }

    @Benchmark
    public void createObjects(long reps) {
        for (long i = 0; i < reps; i++) {
            realm.where(AllTypes.class).findFirst();
        }
    }

    @Benchmark
    public void createQueries(long reps) {
        for (long i = 0; i < reps; i++) {
            realm.where(AllTypes.class);
        }
    }
    @Benchmark
    public void createResults(long reps) {
        for (long i = 0; i < reps; i++) {
            realm.where(AllTypes.class).findAll();
        }
    }

    @Benchmark
    public void createLinkView(long reps) {
        for (long i = 0; i < reps; i++) {
            AllTypes allTypes = realm.where(AllTypes.class).findFirst();
            // To suppress warning
            if (allTypes != null) {
                allTypes.getColumnRealmList();
            }
        }
    }
}