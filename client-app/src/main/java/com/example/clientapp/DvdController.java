package com.example.clientapp;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DvdController {
    @Autowired
    DvdRepository dvds;

    @GetMapping("/dvd/new")
    String enterNewDvd(Model model) {
        model.addAttribute("dvd", new Dvd());
        return "dvd/dvd";
    }

    @GetMapping("/dvd/list")
    String listDvds(Model model) {
        Iterable<Dvd> dvds = this.dvds.findAll();
        dvds.forEach((dvd) -> dvd.setImageBase64(Base64.getEncoder().encodeToString(dvd.getImage())));
        model.addAttribute("dvds", dvds);
        return "dvd/list";
    }

    @GetMapping("/dvd/{id}")
    String showDvd(@PathVariable("id") UUID id, Model model) {
        Dvd dvd = this.dvds.findById(id).get();
        dvd.setImageBase64(Base64.getEncoder().encodeToString(dvd.getImage()));
        model.addAttribute("dvd", dvd);
        return "dvd/dvd";
    }

    @PostMapping("/dvd/{id}/store")
    String storeDvd(@ModelAttribute("dvd") Dvd dvd, @RequestParam("file") MultipartFile file,
                    @AuthenticationPrincipal User user) throws IOException {
        String base64 = dvd.getImageBase64();
        if (base64 != null) {
            dvd.setImage(Base64.getDecoder().decode(base64));
        }
        byte[] b = file.getBytes();
        if (b.length > 0) {
            dvd.setImage(b);
        }
        dvd.setOwner(user);
        this.dvds.save(dvd);
        return "redirect:/dvd/list";
    }

    @PostMapping("/dvd/{id}/remove")
    String removeDvd(@PathVariable("id") UUID id) {
        this.dvds.deleteById(id);
        return "redirect:/dvd/list";
    }
}
