use futures::TryStreamExt;
use mongodb::{
    bson::{doc, oid::ObjectId, Bson},
    error::Result,
    results::{DeleteResult, InsertOneResult, UpdateResult},
    Collection,
};
use serde::{Deserialize, Serialize};

#[allow(async_fn_in_trait)]
pub trait Dao<T>
where
    T: Send + Sync + Unpin + for<'de> Deserialize<'de> + Serialize,
{
    fn get_collection(&self) -> &Collection<T>;

    /// Delete by object id
    async fn delete_by_id(&self, oid: &ObjectId) -> Result<DeleteResult> {
        let filter = doc! { "_id": oid };
        self.get_collection().delete_one(filter).await
    }

    /// Delete an entity by id str
    async fn delete_by_id_str(&self, id: &str) -> Result<DeleteResult> {
        let object_id = ObjectId::parse_str(id).map_err(|e| mongodb::error::Error::custom(e))?;
        self.delete_by_id(&object_id).await
    }

    /// Find all entities in the collection
    async fn find_all(&self) -> Result<Vec<T>> {
        let cursor = self.get_collection().find(doc! {}).await?;
        let entities: Vec<T> = cursor.try_collect().await?;
        Ok(entities)
    }

    /// Find an entity by field
    async fn find_by_field<F: Into<Bson>>(&self, field_name: &str, field_value: F) -> Result<Option<T>> {
        let filter = doc! { field_name: field_value };
        let entity = self.get_collection().find_one(filter).await?;
        Ok(entity)
    }

    /// Find an entity by object id
    async fn find_by_id(&self, oid: &ObjectId) -> Result<Option<T>> {
        let filter = doc! { "_id": oid };
        let entity = self.get_collection().find_one(filter).await?;
        Ok(entity)
    }

    /// Find an entity by id str
    async fn find_by_id_str(&self, id: &str) -> Result<Option<T>> {
        let oid = ObjectId::parse_str(id).map_err(|e| mongodb::error::Error::custom(e))?;
        self.find_by_id(&oid).await
    }

    /// Save an entity
    async fn save(&self, entity: T) -> Result<InsertOneResult> {
        self.get_collection().insert_one(entity).await
    }

    /// Update an entity
    async fn update(&self, oid: &ObjectId, entity: T) -> Result<UpdateResult> {
        let filter = doc! { "_id": oid };
        self.get_collection().replace_one(filter, entity).await
    }
}
