/*
 * Copyright (c) 2015 Carmen Alvarez.
 *
 * This file is part of Nounours.
 *
 * Nounours is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Nounours is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Nounours.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.rmen.nounours;

import ca.rmen.nounours.data.Animation;
import ca.rmen.nounours.data.AnimationImage;
import ca.rmen.nounours.data.Image;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Records an animation.
 *
 * @author Carmen Alvarez
 */
public class NounoursRecorder {
    private long lastFrameTimestamp;
    private long lastPauseTimestamp;
    private long lastResumeTimestamp;
    private Image lastImage;
    private Animation animation;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private void init() {
        lastFrameTimestamp = 0;
        lastPauseTimestamp = 0;
        lastResumeTimestamp = 0;
        lastImage = null;
        animation = null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void start() {
        if (lastFrameTimestamp > 0) throw new IllegalStateException("Already recording");
        long now = System.currentTimeMillis();
        lastFrameTimestamp = now;
        animation = new Animation(
                "nounours-recording-" + now,
                "Nounours Recording" + simpleDateFormat.format(now),
                1000, 1, false,
                false, null);
    }

    public boolean isRecording() {
        return lastFrameTimestamp > 0;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isPaused() {
        return lastPauseTimestamp > 0 && lastResumeTimestamp == 0;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void pause() {
        if (!isRecording()) throw new IllegalStateException("Not recording");
        if (isPaused()) throw new IllegalStateException("Already paused");
        lastPauseTimestamp = System.currentTimeMillis();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void resume() {
        if (!isRecording()) throw new IllegalStateException("Not recording");
        if (!isPaused()) throw new IllegalStateException("Not paused");
        lastResumeTimestamp = System.currentTimeMillis();
    }

    public void addImage(Image image) {
        if (!isRecording()) throw new IllegalStateException("Not recording");
        if (isPaused()) throw new IllegalStateException("Paused");
        // If this is the same as the last image we added, do nothing.
        if (lastImage != null && image.getId().equals(lastImage.getId())) return;
       
        addLastImage();
        lastImage = image;
    }

    public void addImages(Animation animationToAdd) {
        lastFrameTimestamp = System.currentTimeMillis();
        List<AnimationImage> frames = animationToAdd.getImages();
        for (int i = 0; i < animationToAdd.getRepeat(); i++) {
            for (int j = 0; j < frames.size(); j++) {
                AnimationImage frame = frames.get(j);
                float frameDuration = animationToAdd.getInterval() * frame.getDuration();
                animation.addImage(frame.getImage(), frameDuration / 1000);
            }
        }
        lastFrameTimestamp = lastFrameTimestamp + animationToAdd.getDuration();
        lastImage = null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Animation stop() {
        if (!isRecording()) throw new IllegalStateException("Not recording");
        addLastImage();
        Animation result = animation;
        init();
        return result;
    }

    private void addLastImage() {
        long now = System.currentTimeMillis();
        long lastFrameDuration = now - lastFrameTimestamp;
        if (lastResumeTimestamp > 0) {
            long lastPauseDuration = lastResumeTimestamp - lastPauseTimestamp;
            lastFrameDuration -= lastPauseDuration;
            lastPauseTimestamp = 0;
            lastResumeTimestamp = 0;
        }

        if (lastImage != null) {
            float lastRelativeFrameDuration = (float) lastFrameDuration / 1000;
            animation.addImage(lastImage, lastRelativeFrameDuration);
        }

        lastFrameTimestamp = now;

    }
}
