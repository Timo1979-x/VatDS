package by.gto.tools;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Aleks
 */
public class ModalFrameUtil {

    public static void showAsModalFX(final Frame frame, final Stage owner, final int frameSize) {
        /*frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                owner.hide();
                //owner.setEnabled(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                //owner.setEnabled(true);
                owner.show();
                frame.removeWindowListener(this);
            }
        });*/

        /*owner.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if (frame.isShowing()) {
                    frame.setExtendedState(frameSize);
                    frame.toFront();
                } else {

                    owner.removeWindowListener(this);

                }
            }
        });*/

        frame.setVisible(true);
        /*try {
            new EventPump(frame).start();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }*/
    }

    // show the given frame as modal to the specified owner. 
    // NOTE: this method returns only after the modal frame is closed. 
    /*public static void showAsModal(final Frame frame, final Frame owner, final int frameSize) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                if (null != owner) {
                    owner.setEnabled(false);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (null != owner) {
                    owner.setEnabled(true);
                }
                frame.removeWindowListener(this);
            }
        });

        if (null != owner) {
            owner.addWindowListener(new WindowAdapter() {
                @Override
                public void windowActivated(WindowEvent e) {
                    if (frame.isShowing()) {
                        frame.setExtendedState(frameSize);
                        frame.toFront();
                    } else {

                        owner.removeWindowListener(this);

                    }
                }
            });
        }

        frame.setVisible(true);
        try {
            new EventPump(frame).start();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    static class EventPump implements InvocationHandler {

        private final Frame frame;

        public EventPump(Frame frame) {
            this.frame = frame;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return frame.isShowing() ? Boolean.TRUE : Boolean.FALSE;
        }

        // when the reflection calls in this method has to be 
        // replaced once Sun provides a public API to pump events. 
        public void start() throws Exception {
            Class clazz = Class.forName("java.awt.Conditional");
            Object conditional = Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[]{clazz},
                    this);
            Method pumpMethod = Class.forName("java.awt.EventDispatchThread").getDeclaredMethod("pumpEvents", new Class[]{clazz});
            pumpMethod.setAccessible(true);
            pumpMethod.invoke(Thread.currentThread(), new Object[]{conditional});
        }
    }*/
}
