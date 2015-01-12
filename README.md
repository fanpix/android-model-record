# Model Record
A library to help manage the interaction with persisted data on the app when using retrofit, and keep it away from
the UI layer. This lets the Activity/Fragment/View classes request information from the record, and let the
record object manage where it pulls the information from and if/how it is stored.

## Getting Started
To use the library, simply make a class that extends `ModelRecord`, and override the applicable setup configuration method. Each method should include the event that will be fired in the event bus if it succeeds or fails, and the network call that will be executed. For this to work properly with asynchronous requests, the retrofit service needs to use the callback passed in through the `call` method.

### Adding to project
#####Gradle
    dependencies {
        compile 'com.fanpics.opensource:model-record:1.0.0'
    }

### Setting up basic calls

#####Example asynchronous load setup:

    public class MyRecord extends ModelRecord<MyClass> {
    
      @Override
      protected SingleRecordConfiguration setupLoadConfiguration(SingleRecordConfiguration configuration, Object key) {
          configuration.setSuccessEvent(new MyModelLoadSucceededEvent());
          configuration.setFailureEvent(new MyModelLoadFailedEvent());
          configuration.setAsynchronousNetworkCall(new BaseRecordConfiguration.AsyncNetworkCall() {
              @Override
              public void call(Object o, Callback callback) {
                  MyRetrofitService service = getRestAdapater().create(MyRetrofitService.class);
                  service.loadAsynchronously(callback);
              }
          });
          return configuration;
      }
    }
    
Note: `setupLoadConfiguration` sets up a configuration that has been preset up for you by the superclass, which is
usually optimal. If you want more control, however, you can instantiate your own configuration class instead.

#####Example synchronous load setup:

    public class MyRecord extends ModelRecord<MyClass> {
    
      @Override
      protected SingleRecordConfiguration setupLoadConfiguration(SingleRecordConfiguration configuration, Object key) {
          configuration.setSuccessEvent(new MyModelLoadSucceededEvent());
          configuration.setFailureEvent(new MyModelLoadFailedEvent());
          configuration.setSynchronousNetworkCall(new BaseRecordConfiguration.SynchronousNetworkCall() {
            @Override
            public Result call(Object key) {
                MyRetrofitService service = getRestAdapater().create(MyRetrofitService.class);
                return service.loadSynchronously(key);
            }
          });
          return configuration;
      }
    }

You are able to set both asynchronous and synchronous network calls in the same configuration if your record is capable of being accessed both ways.

Note: At this time, only loading supports synchronous calls.

###Setting up local data management
To hook it into your own local data, simply pass in a `RecordCache` for the type of model being managed into the
configuration. The `RecordCache` is a simple interface for managing data that the library will make calls to in
order to sync the local database after retrofit calls finish.

#####Example RecordCache:
    public class MyModelCache implements RecordCache<MyModel> {
      MyModel load(Object key) {
        // Add your loading logic here.
      }

      List<MyModel> loadList(Object key) {
        // Add your loading logic for a list of items here.
      }

      void store(Object key, MyModel model) {
        // Add your persistance logic here.
      }

      void store(Object key, List<MyModel> models) {
        // Add your persistance logic for a list of objects here.
      }

      void clear() {
        // Add your logic for completely clearing the cache here
      }

      void delete(MyModel model) {
        // Add your logic for deleting single object here.
      }
    }
    
    public class MyRecord extends ModelRecord<MyClass> {

        @Override
        protected SingleRecordConfiguration setupLoadConfiguration(SingleRecordConfiguration configuration, Object key) {
            ...
            configuration.setCache(new MyModelCache());
            return configuration;
        }
    }

    
###Using Record to load objects
To use the `ModelRecord` to load objects, make sure the configuration calls for whatever action you wish to perform
have been set up, then simply instantiate the object and make the associated call. Make sure the activity subscribes
to the events being created for the record.

    public class MyActivity extends Activity {
    ...
        @Override
            protected void onCreate(Bundle savedInstanceState) {
            ...
            new MyRecord(this.bus).load(key);
        }

        @Subscribe
        public void onMyModelLoadSucceededEvent(MyModelLoadSucceededEvent event) {
            MyModel myModel = event.getResult();
            // Handle model loaded
            if (event.hasFinished()) {
              // Handle load completed (useful if eager loading)
            }
        }

        @Subscribe
        public void onMyModelLoadFailed(MyModelLoadFailedEvent event) {
            // Handle load failure
        }
    }
    
Note: Loading has multiple actions that can be performed with only setting up a single configuration. Please view the <A href="https://github.com/fanpix/android-model-record/blob/master/app/src/main/java/com/fanpics/opensource/android/modelrecord/ModelRecord.java">ModelRecord source</a> to see all available actions for a model.

Note: ModelRecords take an optional second class in their constructor if you need to deal with metadata from the call. Please view the <A href="https://github.com/fanpix/android-model-record/blob/master/app/src/main/java/com/fanpics/opensource/android/modelrecord/HttpReport.java">HttpReport source</a> for more information.

###Event Objects
Event objects are sent in the bus posts for you to properly manage events.

#####Success Events
Success events inform you that the request has returned information which you can access with `getResult()`, and tell if the request has fully finished loading or if it will potentially send another event with `hasFinished()` (as is the case when eager loading data from the cache).

#####Failure Events
Failure events inform you that the request has failed and pass in the `RetrofitError` which can be accessed with `getError()` if more information about the failure is required.

#License
    Copyright (c) 2014 Fanpics LLC

    The MIT License (MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
