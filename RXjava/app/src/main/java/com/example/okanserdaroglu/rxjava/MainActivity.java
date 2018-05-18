package com.example.okanserdaroglu.rxjava;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    /** 30 yaş ve 30 yaştan küçükler  ObserverAge*/
    /** 30 yaş ve 30 yaştan büyük erkeklerin yaşını 30 diye değiştir*/

    
    private static final String TAG = MainActivity.class.getSimpleName();

    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Age
        Disposable disposableAge = UserObservable().subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread())
                                .filter(new Predicate<User>() {
                                    @Override
                                    public boolean test(User user) throws Exception {
                                        return user.getAge()<= 30;
                                    }
                                }).subscribeWith(ObserverAge());

        compositeDisposable.add(disposableAge);

        //Gender
        Disposable disposableGender =
                UserObservable().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<User>() {
                    @Override
                    public boolean test(User user) throws Exception {
                        return (user.getAge()>=30 && user.getGender().equals("male"));
                    }
                }).map(new Function<User,User >() {
                    @Override
                    public User apply(User user) throws Exception {
                        user.setAge(30);
                        return user;
                    }
                }).subscribeWith(ObserverGender());

        compositeDisposable.add(disposableGender);


    }

    public DisposableObserver<User> ObserverGender () {

        return new DisposableObserver<User>() {
            @Override
            public void onNext(User user) {
                Log.e(TAG,"Gender OnNext " + user.getName()+ " yeni yaş : " + user.getAge());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

    }

    public DisposableObserver<User> ObserverAge () {


        return new DisposableObserver<User>() {
            @Override
            public void onNext(User user) {
                Log.e(TAG,"Age OnNext " + user.getName());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };


    }


    public Observable<User> UserObservable () {

        final List<User> userList = loadUser();

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> e) throws Exception {

                for (User user : userList) {
                    if (!e.isDisposed()) {
                        e.onNext(user);
                    }
                }

                if (!e.isDisposed()) {
                    e.onComplete();
                }
            }
        });

    }

    public List<User>loadUser () {

        List<User>userList = new ArrayList<>();
        userList.add(new User("Okan",30, "male"));
        userList.add(new User("Bakdağ",25, "famale"));
        userList.add(new User("Özge",27, "famale"));
        userList.add(new User("Sinan",27, "male"));
        userList.add(new User("Can",37, "male"));
        userList.add(new User("Ece",28, "famale"));
        userList.add(new User("Yağmur",29, "famale"));
        userList.add(new User("Samet",29, "male"));
        userList.add(new User("Mehmet",40, "male"));
        userList.add(new User("Hasan",31, "male"));

        return userList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
