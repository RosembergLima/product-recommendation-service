package pt.challenge.util;

/**
 * Enum representing the different types of feedback a user can provide on a recommendation.
 */
public enum FeedbackType {
  /**
   * User liked the recommended product.
   */
  LIKED, 
  /**
   * User disliked the recommended product.
   */
  DISLIKED, 
  /**
   * User has already purchased the product.
   */
  PURCHASED, 
  /**
   * User is not interested in this product or category.
   */
  NOT_INTERESTED
}
