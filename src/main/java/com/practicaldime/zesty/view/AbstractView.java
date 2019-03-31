package com.practicaldime.zesty.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public abstract class AbstractView implements ViewBuilder {

    @Override
    public String getEngine() {
        return "freemarker";
    }

    @Override
    public String getLayout() {
        return "/themes/basic/basic.ftl";
    }

    @Override
    public String getTitle() {
        return "Hello";
    }

    @Override
    public String getCharset() {
        return "UTF-8";
    }

    @Override
    public void writeTemplate(String template, String content) {
        try (OutputStream out = new FileOutputStream(new File("www", template));
                WritableByteChannel outChannel = Channels.newChannel(out)) {
            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
            outChannel.write(buffer);
        } catch (IOException ex) {
            throw new ViewException(ex);
        }
    }

    @Override
    public String loadMarkup(String template) {
        try (InputStream ins = new FileInputStream(new File("www", template));
                ReadableByteChannel inChannel = Channels.newChannel(ins)) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            int bytesRead = inChannel.read(buffer); //read into buffer.
            while (bytesRead != -1) {
                buffer.flip();  //make buffer ready for read

                if (buffer.hasRemaining()) {
                    byte[] xfer = new byte[buffer.limit()]; //transfer buffer bytes to a different aray
                    buffer.get(xfer);
                    bytes.write(xfer); // read entire array backing buffer
                }

                buffer.clear(); //make buffer ready for writing
                bytesRead = inChannel.read(buffer);
            }
            return new String(bytes.toByteArray());
        } catch (IOException ex) {
            throw new ViewException(ex);
        }
    }

    @Override
    public String mergeTemplate(String name, String markup) {
//        try {
//            switch (getEngine()) {
//                case "freemarker":
//                    Template template = new Template(name, markup, FtlViewEngine.getConfiguration().getEnvironment());
//                    StringWriter output = new StringWriter();
//                    template.process(getModel(), output);
//                    return output.toString();
//                case "jtwig":
//                    ResourceReference resource = new ResourceReference(ResourceReference.STRING, markup);
//                    JtwigTemplate jtwigTemplate = new JtwigTemplate(TwigViewEngine.getConfiguration().getEnvironment(), resource);
//                    JtwigModel viewModel = JtwigModel.newModel(getModel());
//                    return jtwigTemplate.render(viewModel);
//                case "string":
//                	return DefaultViewEngine.instance().merge(markup, getModel());
//                default:
//                    throw new ViewException("unsupported template engine");
//            }
//        } catch (Exception e) {
//            throw new ViewException(e);
//        }
    	throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String[] getMetaTags() {
        return new String[]{};
    }

    @Override
    public String[] getStyles() {
        return new String[]{};
    }

    @Override
    public String[] getScripts(boolean top) {
        return new String[]{};
    }
}
