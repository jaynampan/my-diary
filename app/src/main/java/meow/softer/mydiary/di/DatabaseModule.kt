package meow.softer.mydiary.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import meow.softer.mydiary.data.local.db.DiaryDatabase
import meow.softer.mydiary.data.local.db.dao.ContactDao
import meow.softer.mydiary.data.local.db.dao.DiaryDao
import meow.softer.mydiary.data.local.db.dao.DiaryItemDao
import meow.softer.mydiary.data.local.db.dao.MemoDao
import meow.softer.mydiary.data.local.db.dao.MemoOrderDao
import meow.softer.mydiary.data.local.db.dao.TopicDao
import meow.softer.mydiary.data.local.db.dao.TopicOrderDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): DiaryDatabase {
        return DiaryDatabase.getDatabase(context)
    }


    @Provides
    fun provideContactDao(appDatabase: DiaryDatabase): ContactDao {
        return appDatabase.contactDao()
    }

    @Provides
    fun provideDiaryDao(appDatabase: DiaryDatabase): DiaryDao {
        return appDatabase.diaryDao()
    }

    @Provides
    fun provideDiaryItemDao(appDatabase: DiaryDatabase): DiaryItemDao {
        return appDatabase.diaryItemDao()
    }

    @Provides
    fun provideMemoDao(appDatabase: DiaryDatabase): MemoDao {
        return appDatabase.memoDao()
    }

    @Provides
    fun provideMemoOrderDao(appDatabase: DiaryDatabase): MemoOrderDao {
        return appDatabase.memoOrderDao()
    }

    @Provides
    fun provideTopicDao(appDatabase: DiaryDatabase): TopicDao {
        return appDatabase.topicDao()
    }

    @Provides
    fun provideTopicOrderDao(appDatabase: DiaryDatabase): TopicOrderDao {
        return appDatabase.topicOrderDao()
    }
}