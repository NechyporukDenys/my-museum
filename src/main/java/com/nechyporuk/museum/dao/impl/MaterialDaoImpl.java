package com.nechyporuk.museum.dao.impl;

import com.nechyporuk.museum.config.HibernateUtil;
import com.nechyporuk.museum.constant.ErrorMessage;
import com.nechyporuk.museum.dao.MaterialDao;
import com.nechyporuk.museum.entity.Material;
import com.nechyporuk.museum.exception.NotDeletedException;
import com.nechyporuk.museum.exception.NotFoundException;
import com.nechyporuk.museum.exception.NotUpdatedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MaterialDaoImpl implements MaterialDao {
  private static Logger logger = LogManager.getLogger(MaterialDaoImpl.class);

  @Override
  public void save(Material entity) {
    Transaction transaction = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      session.save(entity);
      transaction.commit();
      logger.info("Material saved");
    } catch (Exception e) {
      logger.error("Failed to save material: " + e);
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
  }

  @Override
  public List<Material> getAll() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      return session.createQuery("from Material").getResultList();
    } catch (Exception e) {
      logger.error("Failed to get all authors: " + e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(Long id) {
    Transaction transaction = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      Material material = session.get(Material.class, id);
      if (material == null) {
        throw new NotDeletedException(String.format(ErrorMessage.MATERIAL_NOT_DELETED_BY_ID, id));
      }
      session.delete(material);
      transaction.commit();
      logger.info(String.format("Material with id %d successfully removed", id));
    } catch (NotDeletedException e) {
      logger.error("Failed to remove material: " + e);
      e.printStackTrace();
    } catch (Exception e) {
      logger.error("Failed to remove material: " + e);
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
  }

  @Override
  public void update(Material entity) {
    Transaction transaction = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      Material material = session.get(Material.class, entity.getId());
      if (material == null) {
        throw new NotUpdatedException(String.format(ErrorMessage.MATERIAL_NOT_UPDATED_BY_ID, entity.getId()));
      }
      session.merge(entity);
      transaction.commit();
      logger.info(String.format("Material with id %d successfully updated", entity.getId()));
    } catch (NotUpdatedException e) {
      logger.error("Failed to update material: " + e);
      e.printStackTrace();
    } catch (Exception e) {
      logger.error("Failed to update material: " + e);
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
  }

  @Override
  public Optional<Material> getOneById(Long id) {
    Transaction transaction = null;
    Material material = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      material = session.get(Material.class, id);
      if (material == null) {
        throw new NotFoundException(String.format(ErrorMessage.MATERIAL_NOT_FOUND_BY_ID, id));
      }
      transaction.commit();
    } catch (NotFoundException e) {
      logger.error("Failed to get material by id: " + e);
      e.printStackTrace();
      return Optional.empty();
    } catch (Exception e) {
      logger.error("Failed to get material by id: " + e);
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
    return Optional.of(material);
  }
}
