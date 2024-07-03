# you will need two CSV files: ratings.csv and movies.csv in below Format 
# ratings.csv

# userId,movieId,rating
# 1,1,4.0
# 1,3,4.0
# 1,6,4.0
# ...
# movies.csv:

# movieId,title
# 1,Toy Story (1995)
# 2,Jumanji (1995)
# 3,Grumpier Old Men (1995)
# ...

import pandas as pd
from surprise import Dataset, Reader, SVD
from surprise.model_selection import cross_validate, train_test_split
from surprise import accuracy
from collections import defaultdict

class MovieRecommendationSystem:
    def __init__(self, ratings_path, movies_path):
        self.ratings_path = ratings_path
        self.movies_path = movies_path
        self.data = None
        self.trainset = None
        self.testset = None
        self.algo = SVD()

    def load_data(self):
        ratings = pd.read_csv(self.ratings_path)
        reader = Reader(rating_scale=(0.5, 5))
        self.data = Dataset.load_from_df(ratings[['userId', 'movieId', 'rating']], reader)

    def train_test_split(self):
        self.trainset, self.testset = train_test_split(self.data, test_size=0.2)

    def train(self):
        self.algo.fit(self.trainset)

    def evaluate(self):
        predictions = self.algo.test(self.testset)
        rmse = accuracy.rmse(predictions)
        return rmse

    def get_top_n(self, predictions, n=10):
        top_n = defaultdict(list)
        for uid, iid, true_r, est, _ in predictions:
            top_n[uid].append((iid, est))
        for uid, user_ratings in top_n.items():
            user_ratings.sort(key=lambda x: x[1], reverse=True)
            top_n[uid] = user_ratings[:n]
        return top_n

    def predict(self):
        predictions = self.algo.test(self.testset)
        top_n = self.get_top_n(predictions, n=10)
        return top_n

    def get_movie_titles(self, movie_ids):
        movies = pd.read_csv(self.movies_path)
        movie_titles = movies[movies['movieId'].isin(movie_ids)]
        return movie_titles[['movieId', 'title']]

    def recommend_for_user(self, user_id, n=10):
        predictions = self.predict()
        user_recommendations = predictions[user_id]
        movie_ids = [iid for iid, _ in user_recommendations]
        return self.get_movie_titles(movie_ids)

    def full_workflow(self):
        self.load_data()
        self.train_test_split()
        self.train()
        self.evaluate()
        return self.predict()

ratings_path = 'ratings.csv'
movies_path = 'movies.csv'

recommender = MovieRecommendationSystem(ratings_path, movies_path)
recommender.full_workflow()

user_id = 1
recommendations = recommender.recommend_for_user(user_id)
print(recommendations)

