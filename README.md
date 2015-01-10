# Model Record
A library to help manage the interaction with persisted data on the app when using retrofit, and keep it away from
the UI layer. This lets the Activities/Fragments/Views classes request information from the record, and let the
record object manage where it pulls the information from and if/how it is stored.

# Getting Started
To use the library, simply make a class that extends `ModelRecord`, and override the applicable setup configuration method. Each method should include the event that will be fired in the event bus if it succeeds or fails, and the network call that will be executed. For this to work properly with asynchronous requests, the retrofit service needs to use the callback passed in through the `call` method.

### Setting up basic calls

#####Example asynchronous load setup:

    public class MyRecord extends ModelRecord<MyClass> {
    
      @Override
      protected SingleRecordConfiguration setupLoadConfiguration(SingleRecordConfiguration configuration, Object key) {
          configuration.setSuccessEvent(new LoadSucceededEvent());
          configuration.setFailureEvent(new LoadFailedEvent());
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

#####Example synchronous load setup:

    public class MyRecord extends ModelRecord<MyClass> {
    
      @Override
      protected SingleRecordConfiguration setupLoadConfiguration(SingleRecordConfiguration configuration, Object key) {
          configuration.setSuccessEvent(new LoadSucceededEvent());
          configuration.setFailureEvent(new LoadFailedEvent());
          configuration.setSynchronousNetworkCall(new BaseRecordConfiguration.SynchronousNetworkCall() {
            @Override
            public Result call(Object key) {
                MyRetrofitService service = getRestAdapater().create(MyRetrofitService.class);
                return loadSynchronously(key);
            }
          });
          return configuration;
      }
    }

You are able to set both asynchronous and synchronous network calls in the same configuration if your record is capable of being accessed both ways.

Note: At this time, only loading supports synchronous calls.

###Setting up local data management
To hook it into your own local data, simply pass in a `RecordCache` for the type of model being managed into the configuration. The `RecordCache` is a simple interface for managing data that the library will make calls to in order to sync the local database after retrofit calls finish.

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

    
