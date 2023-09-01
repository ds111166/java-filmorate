package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendStorage implements FriendStorage {

    private final Map<Friendship, Long> friendships;

    public InMemoryFriendStorage() {
        this.friendships = new HashMap<>();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        final long difference = userId - friendId;
        Friendship pair1to2 = Friendship.builder().userId(userId).friendId(friendId).build();
        Friendship pair2to1 = Friendship.builder().userId(friendId).friendId(userId).build();
        final boolean is2to1 = friendships.containsKey(pair2to1);
        if (is2to1) {
            friendships.put(pair2to1, friendships.get(pair2to1) + difference);
        } else {
            friendships.put(pair1to2, difference);
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        final long difference = userId - friendId;
        Friendship pair1to2 = Friendship.builder().userId(userId).friendId(friendId).build();
        Friendship pair2to1 = Friendship.builder().userId(friendId).friendId(userId).build();
        final boolean is1to2 = friendships.containsKey(pair1to2);
        final boolean is2to1 = friendships.containsKey(pair2to1);
        if (difference == 0) {
            if (is1to2) {
                friendships.remove(pair1to2);
            }
            if (is2to1) {
                friendships.remove(pair2to1);
            }
            return;
        }
        if (is1to2) {
            if (friendships.get(pair1to2) == 0) {
                friendships.put(pair1to2, difference);
            } else {
                friendships.remove(pair1to2);
            }
        }
        if (is2to1) {
            if (friendships.get(pair2to1) == 0) {
                friendships.put(pair2to1, -1 * difference);
            }
        }
    }

    @Override
    public Set<Long> getIdsFriendsForUser(long userId) {
        Set<Long> ids = new HashSet<>();
        for (Map.Entry<Friendship, Long> entry : friendships.entrySet()) {
            final Friendship friendship = entry.getKey();
            if (friendship.getUserId() == userId) {
                ids.add(friendship.getFriendId());
            }
            if (entry.getValue() == 0 && friendship.getFriendId() == userId) {
                ids.add(friendship.getUserId());
            }
        }
        return ids;
    }

    @Override
    public Set<Long> getMutualFriendsOfUsers(long userId, long otherId) {
        return getIdsFriendsForUser(userId)
                .stream()
                .filter(id -> getIdsFriendsForUser(otherId).contains(id))
                .collect(Collectors.toSet());
    }
}
