package com.example.dellxps15.roomwordsample2;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ProductRepository {

    private ProductDao mProductDao;
    private LiveData<List<Products>> mAllProducts;
    private Products iAllProducts;

    ProductRepository(Application application) {
        ProductRoomDatabase db = ProductRoomDatabase.getDatabase(application);
        mProductDao = db.productDao();
        mAllProducts = mProductDao.getAllProducts();
    }

    LiveData<List<Products>> getAllProducts() {
        return mAllProducts;
    }
    Products getProductById(int id) {
        try{
            iAllProducts = new findItemAsyncTask(mProductDao).execute(new Integer(id)).get();
        } catch(Exception e){
            e.printStackTrace();
        } return iAllProducts;
    }

    public void insert (Products product) {
        new insertAsyncTask(mProductDao).execute(product);
    }

    private static class insertAsyncTask extends AsyncTask<Products, Void, Void> {

        private ProductDao mAsyncTaskDao;
        insertAsyncTask(ProductDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Products... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class findItemAsyncTask extends AsyncTask<Integer, Void, Products> {

        private ProductDao mAsyncTaskDao;
        findItemAsyncTask(ProductDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Products doInBackground(Integer... id) {
            Products result = mAsyncTaskDao.getProductById(id[0].intValue());
            return result;
        }
    }

}
