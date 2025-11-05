/**
 * Custom middleware for json-server to handle post interaction endpoints
 */

module.exports = (req, res, next) => {
  // Handle POST /posts/:id/interact
  const interactMatch = req.url.match(/^\/posts\/(\d+)\/interact$/);

  if (req.method === 'POST' && interactMatch) {
    const postId = parseInt(interactMatch[1]);
    const db = req.app.db;

    // Find the post by id
    const post = db.get('posts').find({ id: postId }).value();

    if (!post) {
      return res.status(404).json({ error: 'Post not found' });
    }

    // Parse the interaction request
    const { type } = req.body;

    if (type === 'like') {
      // Toggle like status
      const newLiked = !post.liked;
      const newLikeCount = newLiked ? post.likeCount + 1 : post.likeCount - 1;

      // Update the post
      db.get('posts')
        .find({ id: postId })
        .assign({ liked: newLiked, likeCount: newLikeCount })
        .write();

      return res.status(200).json({ success: true, liked: newLiked, likeCount: newLikeCount });
    } else if (type === 'share') {
      // Increment share count
      const newShareCount = (post.shareCount || 0) + 1;

      db.get('posts')
        .find({ id: postId })
        .assign({ shareCount: newShareCount })
        .write();

      return res.status(200).json({ success: true, shareCount: newShareCount });
    } else {
      return res.status(400).json({ error: 'Invalid interaction type' });
    }
  }

  // Continue to json-server router
  next();
};
